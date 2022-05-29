using AutoMapper;
using DiscussionService.Dtos;
using DiscussionService.Models;

namespace DiscussionService.Profiles
{
    public class AnswerProfile : Profile
    {
        public AnswerProfile()
        {
            CreateMap<CreateAnswerDto, Answer>();
            CreateMap<Answer, GetAnswerDto>();
            CreateMap<UpdateAnswerDto, Answer>();
        }
    }
}