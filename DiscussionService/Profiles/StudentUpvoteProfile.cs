using AutoMapper;
using DiscussionService.Dtos;
using DiscussionService.Models;

namespace DiscussionService.Profiles
{
    public class StudentUpvoteProfile : Profile
    {
        public StudentUpvoteProfile()
        {
            CreateMap<StudentUpvote, GetStudentUpvoteDto>().ForMember((dest) => dest.Student, opt => opt.MapFrom(src => src.Student));
        }
    }
}