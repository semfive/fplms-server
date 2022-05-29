using AutoMapper;
using DiscussionService.Dtos;
using DiscussionService.Models;

namespace DiscussionService.Profiles
{
    public class QuestionProfile : Profile
    {
        public QuestionProfile()
        {
            CreateMap<CreateQuestionDto, Question>();
            CreateMap<Question, GetQuestionDto>();
            CreateMap<UpdateQuestionDto, Question>();
        }
    }
}